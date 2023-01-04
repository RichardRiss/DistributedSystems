%%%-------------------------------------------------------------------
%%% @author riss
%%% @copyright (C) 2023, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 04. Jan 2023 13:04
%%%-------------------------------------------------------------------
-module(ex10).
-author("riss").

%% API

-export([
        clock/2,
        ticker/2,
        daemon/1,
        run_daemon/0
        ]).


%%%%%%%%%%%%%%%%%%
%% Test process
%%%%%%%%%%%%%%%%%%
run_daemon() ->
  N = 4,
  DaemonPid = spawn(?MODULE, daemon, [N]),
  timer:sleep(5000),
  DaemonPid ! synchronize,
  timer:sleep(5000),
  DaemonPid ! stop.




%%%%%%%%%%%%%%%%%%
%% Daemon process
%%%%%%%%%%%%%%%%%%
daemon(Client_Number) ->
  io:format("Spawning ~p clock processes. ~n", [Client_Number]),
  IntClock =  spawn(?MODULE, clock, [1000, "Daemon"]),
  daemon(Client_Number, IntClock, []).

daemon(IntClock, Processes) ->
  receive
    synchronize ->
      io:format("Daemon Synchronization invoked. ~n"),
      synchronize(IntClock, Processes),
      daemon(IntClock, Processes);
    stop ->
      io:format("Daemon Stop invoked. ~n"),
      stop_daemon(lists:append(Processes,[IntClock])),
      exit(self(), stop_called)
  end.

daemon(0,IntClock, Processes)->
  daemon(IntClock, lists:flatten(Processes));
daemon(N, IntClock, Processes) ->
  %% 500-3000 ms
  TimeDelay = (rand:uniform(26)+4) * 100,
  Pid = spawn(?MODULE, clock, [TimeDelay, N]),
  io:format("Clock process ~p spawned with delay ~p. ~n", [N, TimeDelay]),
  daemon(N - 1, IntClock, [Pid, Processes]).

%%Stop Function
stop_daemon([]) ->
  ok;
stop_daemon(PIDs) ->
  PID = hd(PIDs),
  RemPIDs = tl(PIDs),
  PID ! stop,
  stop_daemon(RemPIDs).


%%Sync Function
synchronize(IntClock, Processes) ->
  SysTime = get_time(IntClock),
  Deltas = lists:map(fun(PID) ->
    get_skew(PID, SysTime)
    end, Processes),
  Sum = lists:sum([X || X <- Deltas]),
  Length = length(Deltas),
  MeanDelta = Sum/Length,
  IntClock ! {adjust, MeanDelta},
  synchronize(MeanDelta, IntClock, Processes).

synchronize(MeanDelta, IntClock, Processes) ->
  io:format("Synchronizing a Delta of ~p. ~n", [MeanDelta]),
  lists:foreach(fun(PID) ->
    SysTime = get_time(IntClock),
    PID ! {set, SysTime}
                end, Processes),
  io:format("Synchronization sucessfull. ~n").


%%%%%%%%%%%%%%%%%%
%% Clock process
%%%%%%%%%%%%%%%%%%
clock(Time, Name) ->
  TickerPID = spawn(?MODULE, ticker, [self(), Time]),
  clock(0, TickerPID, Name).

clock(Value, TickerPID, Name) ->
  io:format("Clock ~p is at value ~p.~n", [Name, Value]),
  receive
    {tick, TickerPID} ->
      clock(Value + 1, TickerPID, Name);
    {get_skew, SysTime, Pid} ->
      Skew = SysTime - Value,
      io:format("Clock ~p returned Skew ~p. ~n",[Name, Skew]),
      Pid ! {skew, Skew},
      clock(Value, TickerPID, Name);
    {adjust, Delta} ->
      NewValue = Value + Delta,
      io:format("Clock ~p adjusted to ~p. ~n",[Name,NewValue]),
      clock(NewValue, TickerPID, Name);
    {get, PID} ->
      PID ! {clock, Value},
      clock(Value, TickerPID, Name);
    {set, NewVal} ->
      io:format("Set Clock ~p to ~p ~n", [Name, NewVal]),
      clock(NewVal, TickerPID, Name);
    stop ->
      io:format("Terminate Clock process ~p. ~n", [Name]),
      exit(self(), stop_called)
  end.

ticker(PID, Time) ->
  link(PID),
  process_flag(trap_exit, true),
  receive
    {'EXIT', ExtPID, Reason} ->
      io:format("Exit invoked for reason ~p by Process-ID ~p ~n", [Reason, ExtPID])
  after
    Time ->
      PID ! {tick, self()},
      ticker(PID, Time)
  end.

get_time(PID) ->
  PID ! {get, self()},
  receive
    {clock, Value} ->
      Value
  after 1000 ->
    0
  end.


get_skew(PID, SysTime) ->
  PID ! {get_skew, SysTime, self()},
  receive
    {skew, Skew} ->
      Skew
  after 1000 ->
    0
  end.