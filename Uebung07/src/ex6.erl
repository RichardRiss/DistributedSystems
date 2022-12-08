%%%-------------------------------------------------------------------
%%% @author RR
%%% @copyright (C) 2022, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 27. Nov 2022 20:20
%%%-------------------------------------------------------------------
-module(ex6).
-author("RR").


%<<<<<<<<<<<< Sequential Erlang <<<<<<<<<<<<<<<<<<<

%%API
-export([convert/2,
        maxitem/1,
        diff/3,
        counter/0,
        start_counter/0]).

%%%%%%%%%%%%%
%% Converter
%%%%%%%%%%%%%

%1 inch = 2,45 cm
-define(INCH, 2.54).
-define(CM, 1/2.54).


convert(VAL, inch) -> { cm ,VAL * ?INCH};
convert(VAL, cm) -> { inch ,VAL * ?CM};
convert(VAL, Unit) -> io:format("Unit ~p not found. Value ~p not converted \n", [Unit, VAL]).

%%%%%%%%%%%%%%%%%%%%
%% Max item of list
%%%%%%%%%%%%%%%%%%%%
maxitem([]) ->
        io:format("~p	~p ~n", ["Emptylist", []]),
        0;
maxitem([H|T]) -> maxitem(T,H).

maxitem([H|T], MAX) when H > MAX ->
        io:format("~p, ~p,	~p ~n", ["Max Value:", MAX, [H|T]]),
        maxitem(T, H);
maxitem([_|T], MAX)              ->
        io:format("~p, ~p,	~p ~n", ["Max Value:", MAX, T]),
        maxitem(T, MAX);
maxitem([],    MAX)              ->
        MAX.

%%%%%%%%%%%%%%%%%%%
%% Differentiation
%%%%%%%%%%%%%%%%%%%
% solve like
% ex6:diff(fun(X) -> 2 * (X*X*X) - 12 * X + 3 end, 3, 1.0e-10).

diff(F, X, H) ->
  (F( X + H ) - F( X - H )) / (2 * H).


%%%%%%%%%%%%%%%%%%%
%% Counter
%%%%%%%%%%%%%%%%%%%
counter() ->
        counter(0).

counter(Value) ->
        io:format("Counter is at value ~p ~n", [Value]),
        receive
                reset ->
                        io:format("Reset Counter ~n"),
                        counter(0);
                up ->
                        io:format("Up ~n"),
                        counter(Value + 1);
                down ->
                        io:format("Down ~n"),
                        counter(Value - 1)
        end.

start_counter() ->
        PID = spawn(ex6, counter, []),
        io:format("Counter started with PID ~p~n", [PID]),
        PID ! up,
        PID ! up,
        PID ! up,
        PID ! up,
        PID ! down,
        PID ! reset,
        PID ! up,
        PID ! up,
        PID ! down,
        PID ! down,
        PID ! down.













