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
        p2/0,
        c/0,
        p2_c_echo/0]).


%%%%%%%%%%%%%%%%%%%%%%%%%
%% Filter P2
%%%%%%%%%%%%%%%%%%%%%%%%%
p2() ->
  Counter = 0,
  Echo = spawn_link(?MODULE, echo, []),
  p2(Counter, Echo).


p2(Counter, Echo) ->
  receive
    {filter, ID} ->
      Counter_New = Counter + 1,
      case Counter_New rem 2 of
        0 ->
          Echo ! {filter, ID},
          p2(Counter_New, Echo);
        _ ->
          p2(Counter_New, Echo)
      end;
    _Other ->
      p2(Counter, Echo)
  end.

%%%%%%%%%%%%%%%%%%%%%%%%%
%% Collector
%%%%%%%%%%%%%%%%%%%%%%%%%
c() ->
 SenderPID = 0,
 MsgList = [],
  c(MsgList, SenderPID).

c(MsgList, SenderPID) ->
  receive
    {set_sender, Pid} ->
      io:format("Collector received new Sender-PID. ~n"),
      c(MsgList, Pid);
    reset ->
      io:format("Collector received Reset Signal. ~n"),
      c([], SenderPID);
    {filter, Msg} ->
      io:format("Collector received new Item ~p. ~n",[Msg]),
      NewList = MsgList ++ [Msg],
      case SenderPID of
        0 ->
          c(NewList, SenderPID);
        _ ->
          SenderPID ! {filter, NewList},
          c(NewList, SenderPID)
      end
  end.


%%%%%%%%%%%%%%%%%%%%%%%%%
%% Pipeline
%%%%%%%%%%%%%%%%%%%%%%%%%
p2_c_echo() ->
  Counter = 0,
  C = spawn_link(?MODULE, c, []),
  Echo = spawn_link(?MODULE, echo, []),
  C ! {set_sender, Echo},
  p2(Counter, C).




%%%%%%%%%%%%%%%%%%%%%%%%%
%% Utility
%%%%%%%%%%%%%%%%%%%%%%%%%
echo() ->
  receive
    stop -> ok;
    Msg ->
      io:format("Echo: ~p\n",[Msg]),
      echo()
  end.


start() ->
  P2 = spawn(?MODULE, p2,[]),
  P2 ! {filter, 1},
  P2 ! {filter, 2},
  P2 ! {filter, 3},
  P2 ! {filter, 4},
  P2 ! {filter, 5},
  timer:sleep(500),

  C = spawn(?MODULE, c, []),
  Echo = spawn(?MODULE, echo, []),
  C ! {set_sender, Echo},
  C ! {filter, 1},
  C ! {filter, b},
  C ! {filter, 3},
  C ! reset,
  timer:sleep(500),

  Pipeline = spawn(?MODULE, p2_c_echo,[]),
  Pipeline ! {filter,120},
  Pipeline ! {filter,109},
  Pipeline ! {filter,150},
  Pipeline ! {filter,101},
  Pipeline ! {filter,155},
  Pipeline ! {filter,114},
  Pipeline ! {filter,189},
  Pipeline ! {filter,114},
  Pipeline ! {filter,27},
  Pipeline ! {filter,121},
  Pipeline ! {filter,68},
  Pipeline ! {filter,32},
  Pipeline ! {filter,198},
  Pipeline ! {filter,99},
  Pipeline ! {filter,33},
  Pipeline ! {filter,104},
  Pipeline ! {filter,164},
  Pipeline ! {filter,114},
  Pipeline ! {filter,212},
  Pipeline ! {filter,105},
  Pipeline ! {filter,194},
  Pipeline ! {filter,115},
  Pipeline ! {filter,24},
  Pipeline ! {filter,116},
  Pipeline ! {filter,148},
  Pipeline ! {filter,109},
  Pipeline ! {filter,173},
  Pipeline ! {filter,97},
  Pipeline ! {filter,8},
  Pipeline ! {filter,115},
  Pipeline ! {filter,191},
  Pipeline ! {filter,33},

  timer:sleep(500),
  ok.
