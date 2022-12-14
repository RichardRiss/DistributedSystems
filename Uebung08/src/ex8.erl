%%%-------------------------------------------------------------------
%%% @author riss
%%% @copyright (C) 2022, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 14. Dez 2022 13:10
%%%-------------------------------------------------------------------
-module(ex8).
-author("riss").

%% API
-export([clock/1,
        start_clock/1,
        get/1,
        ticker/2,
        timer/3,
        start_timer/0
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
      exit(TickerPID, ok),
      exit(self(), ok)
  end.

get(PID) ->
  PID ! {get, self()},
  receive
    {clock, Value} ->
      io:format("Get returned value ~p ~n", [Value])
  end.

ticker(PID, Time) ->
  receive
  after
    Time ->
      PID ! {tick, self()},
      ticker(PID, Time)
  end.


start_clock(Time) ->
  PID = spawn(?MODULE, clock, [Time]),
  PID ! {set ,0},
  timer:sleep(3000),
  spawn(?MODULE, get, [PID]),
  timer:sleep(2000),
  PID ! pause,
  timer:sleep(2000),
  PID ! resume,
  timer:sleep(2000),
  PID ! {set ,0},
  timer:sleep(1000),
  PID ! stop.


%%%%%%%%%%%%%%%%%%
%% Timer process
%%%%%%%%%%%%%%%%%%
timer(Time, Func, Parameter) ->
  TickerPID = spawn(?MODULE, ticker, [self(), Time]),
  io:format("Timer started with ~p ms ~n",[Time]),
  receive
    {tick,TickerPID} ->
      io:format("Timer ended. Calling Function ~p ~n",[Func]),
      ?MODULE:Func(Parameter)
  end.

start_timer() ->
  spawn(?MODULE, timer, [3000, start_clock, 1000]).

