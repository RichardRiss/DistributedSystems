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
        clock/1,
        get/1,
        ticker/2
        ]).





%%%%%%%%%%%%%%%%%%
%% Clock process
%%%%%%%%%%%%%%%%%%
clock(Time) ->
  TickerPID = spawn(?MODULE, ticker, [self(), Time]),
  clock(0, false, TickerPID).

clock(Value, Paused, TickerPID) ->
  if
    Paused ->
      io:format("Clock is at value ~p. [Stopped]~n", [Value]);
    true ->
      io:format("Clock is at value ~p. [Running] ~n", [Value])
  end,
  receive
    {tick,TickerPID} ->
      if
        Paused ->
          clock(Value, Paused, TickerPID);
        true ->
          clock(Value + 1, Paused, TickerPID)
      end;
    {set, NewVal} ->
      io:format("Set Clock to ~p ~n", [NewVal]),
      clock(NewVal, Paused, TickerPID);
    {get, PID} ->
      io:format("Get requested. ~n"),
      PID ! {clock, Value},
      clock(Value, true, TickerPID);
    pause ->
      io:format("Pause Clock. ~n"),
      clock(Value, true, TickerPID);
    resume ->
      io:format("Resume Clock. ~n"),
      clock(Value, false, TickerPID);
    stop ->
      io:format("Terminate Clock process. ~n"),
      exit(self(), stop_called)
  end.

get(PID) ->
  PID ! {get, self()},
  receive
    {clock, Value} ->
      io:format("Get returned value ~p ~n", [Value])
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
