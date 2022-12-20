%%%-------------------------------------------------------------------
%%% @author riss
%%% @copyright (C) 2022, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 14. Dez 2022 13:10
%%%-------------------------------------------------------------------
-module(ex9).
-author("riss").

%% API
-export([echo/0,
        start/0,
        p2/0]).


%%%%%%%%%%%%%%%%%%%%%%%%%
%% Filters and Pipelines
%%%%%%%%%%%%%%%%%%%%%%%%%
p2() ->
  Counter = 0,
  p2(Counter).

p2(Counter) ->
  receive
    {filter, ID} ->
      Counter_New = Counter +1,
      case Counter_New rem 2 of
        0 ->
          Echo = spawn(?MODULE, echo, []),

      end
  end.


echo() ->
  receive
    stop -> ok;
    Msg -> io:format("Echo: ~p\n",[Msg]), echo()
  end.


start() ->
  Echo = spawn(?MODULE, p2,[]),

  P2 = Echo,

  P2!{filter,120},
  P2!{filter,109},
  P2!{filter,150},
  P2!{filter,101},
  P2!{filter,155},
  P2!{filter,114},
  P2!{filter,189},
  P2!{filter,114},
  P2!{filter,27},
  P2!{filter,121},
  P2!{filter,68},
  P2!{filter,32},
  P2!{filter,198},
  P2!{filter,99},
  P2!{filter,33},
  P2!{filter,104},
  P2!{filter,164},
  P2!{filter,114},
  P2!{filter,212},
  P2!{filter,105},
  P2!{filter,194},
  P2!{filter,115},
  P2!{filter,24},
  P2!{filter,116},
  P2!{filter,148},
  P2!{filter,109},
  P2!{filter,173},
  P2!{filter,97},
  P2!{filter,8},
  P2!{filter,115},
  P2!{filter,191},
  P2!{filter,33},

  ok.
