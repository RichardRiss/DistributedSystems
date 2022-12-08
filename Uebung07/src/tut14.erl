%%%-------------------------------------------------------------------
%%% @author Acer
%%% @copyright (C) 2022, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 27. Nov 2022 23:00
%%%-------------------------------------------------------------------
-module(tut14).
-export([start/0, say_something/2]).

say_something(What, 0) ->
  done;

say_something(What, Times) ->
  io:format("~p~n", [What]),
  say_something(What, Times - 1).

start() ->
  spawn(tut14, say_something, [hello, 3]),
  spawn(tut14, say_something, [goodbye, 3]).
