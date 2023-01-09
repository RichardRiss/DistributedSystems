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
        bully/2
        ]).


%%%%%%%%%%%%%%%%%%%%%%%%
%% Setup
%%%%%%%%%%%%%%%%%%%%%%%%
setup(NumProcesses)->
  io:format("Spawning ~p bully processes. ~n .", [NumProcesses]),
  setup(NumProcesses,[]).

setup(0, PidProcesses) ->
  lists:map(fun(Pid) ->
    rpc(Pid,{group, PidProcesses})
            end, PidProcesses),
  start_election(PidProcesses);
setup(NumProcesses, PidProcesses)->
  Pid = spawn(?MODULE, bully, []),
  setup(NumProcesses - 1, lists:append(PidProcesses,[Pid])).



%%%%%%%%%%%%%%%%%%%%%%%%
%% Bully
%%%%%%%%%%%%%%%%%%%%%%%%
bully() ->
  io:format("Bully started with election value ~p ~n .", [self()]),
  receive
    {PidSender, {group, PidProcesses}} ->
      PidSender ! ok,
      bully(0, PidProcesses)
  end.

bully(PidCoordinator, Processes) ->
  SelflessList = lists:filter(fun(X) -> X /= self() end, Processes),
  receive
    {PidSender, election} ->
      PidSender ! ok,
      FilteredList = lists:filter(fun(X) -> X > self() end, Processes),
      if
        length(FilteredList) == 0 ->
          lists:foreach(fun(Pid) ->
            Pid ! {coordinator, self()}
          end, SelflessList),
          bully(self(), Processes);
        true ->
          AnswerList = lists:map(fun(Pid) ->
            rpc(Pid, election)
         end, FilteredList),
          FilteredAnswerList = lists:filter(fun(X) -> X /= unreachable end, AnswerList),
          if
            length(FilteredAnswerList) > 0 ->
              bully(PidCoordinator, Processes);
            true ->
              lists:foreach(fun(Pid) ->
                Pid ! {coordinator, self()}
              end, SelflessList)
          end
      end;
    {coordinator, PidNewCoordinator} ->
      io:format("New Coordinator ~p elected for process ~p ~n .", [PidNewCoordinator, self()]),
      bully(PidNewCoordinator, Processes)
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


start_election(Processes)->
  PidProcess = lists:nth(rand:uniform(length(Processes)),Processes),
  io:format("Process ~p starts election. ~n",[PidProcess]),
  PidProcess ! {self(), election}.