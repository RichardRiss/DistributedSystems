%%%-------------------------------------------------------------------
%%% @author riss
%%% @copyright (C) 2023, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 09. Jan 2023 19:52
%%%-------------------------------------------------------------------
-module(ex11).
-author("riss").

%% API
-export([
        setup/1,
        bully/0,
        bully/3,
        counter/0
        ]).


%%%%%%%%%%%%%%%%%%%%%%%%
%% Setup
%%%%%%%%%%%%%%%%%%%%%%%%
setup(NumProcesses)->
  io:format("Spawning ~p bully processes. ~n", [NumProcesses]),
  setup(NumProcesses,[]).

setup(0, PidProcesses) ->
  PidCounter = spawn(?MODULE, counter,[]),
  lists:map(fun(Pid) ->
    rpc(Pid,{group, PidProcesses, PidCounter})
            end, PidProcesses),
  start_election(PidProcesses),
  timer:sleep(500),
  get_final_coordinator(PidProcesses),
  get_final_counter(PidCounter);
setup(NumProcesses, PidProcesses)->
  Pid = spawn(?MODULE, bully, []),
  setup(NumProcesses - 1, lists:append(PidProcesses,[Pid])).



%%%%%%%%%%%%%%%%%%%%%%%%
%% Bully
%%%%%%%%%%%%%%%%%%%%%%%%
bully() ->
  io:format("Bully started with election value ~p. ~n", [self()]),
  receive
    {PidSender, {group, PidProcesses, PidCounter}} ->
      PidSender ! ok,
      bully(0, PidProcesses, PidCounter)
  end.

bully(PidCoordinator, Processes, PidCounter) ->
  SelflessList = lists:filter(fun(X) -> X /= self() end, Processes),
  receive
    {PidSender, election} ->
      PidCounter ! {PidSender, election},
      PidSender ! {self(),alive},
      % Find bigger Election values
      GreaterPidList = lists:filter(fun(X) -> X > self() end, Processes),
      % Request election from those PIDs
      AnswerList = lists:map(fun(Pid) ->
          rpc(Pid, election)
      end, GreaterPidList),
      % Filter for alive Processes
      FilteredAnswerList = lists:filter(fun(X) -> X /= unreachable end, AnswerList),
      if
        length(FilteredAnswerList) > 0 ->
          bully(PidCoordinator, Processes, PidCounter);
        true ->
          lists:foreach(fun(Pid) ->
            Pid ! {coordinator, self()}
          end, SelflessList),
          bully(self(), Processes, PidCounter)
      end;
    {coordinator, PidNewCoordinator} ->
      io:format("New Coordinator ~p elected for process ~p. ~n", [PidNewCoordinator, self()]),
      bully(PidNewCoordinator, Processes, PidCounter);
    {PidSender, get_coordinator} ->
      PidSender ! {self(), PidCoordinator},
      bully(PidCoordinator, Processes, PidCounter)
  end.

%%%%%%%%%%%%%%%%%%%%%%%%
%% Counter
%%%%%%%%%%%%%%%%%%%%%%%%
counter()->
  counter(0).

counter(Num)->
  receive
    {PidSender, election}->
      counter(Num+1);
    {PidSender, get_value}->
      PidSender ! {self(), Num},
      counter(Num)
  end.

%%%%%%%%%%%%%%%%%%%%%%%%
%% Aux
%%%%%%%%%%%%%%%%%%%%%%%%

rpc(Pid, Request) ->
  Pid ! {self(), Request},
  receive
    {Pid, Response} ->
      Response
  after 250 ->
    unreachable
  end.

get_final_coordinator(Processes)->
  lists:foreach(fun(Pid) ->
    Coordinator = rpc(Pid, get_coordinator),
    io:format("Process ~p final Coordinator is ~p. ~n", [Pid, Coordinator])
    end, Processes).

start_election(Processes)->
  PidProcess = lists:nth(rand:uniform(length(Processes)),Processes),
  io:format("Process ~p starts election. ~n",[PidProcess]),
  PidProcess ! {self(), election},
  receive
    {PidProcess, State} ->
      ok
  end.

get_final_counter(Pid)->
  Pid ! {self(), get_value},
  receive
    {Pid, Value}->
      io:format("~p election messages were exchanged. ~n",[Value])
    after 500 ->
      timeout
  end.