-module(helloworld).
-export([start/0,person/0,ecco/0]).

suffix(S,N) when length(S) =< N -> "..." ++ S;
suffix([_|T],N) -> suffix(T,N).

person() ->
   receive
      {start,Pid} ->
         S = "Hvad drikker Moller",
         io:fwrite("[says]:  " ++ S ++ "\n"),
         Pid ! {self(), {message,S}};
      {message, S} ->
         io:fwrite("[hears]: " ++ S ++ "\n")
   end,
   person().
   
ecco() ->
   receive
      {Sender,{message,S}} ->
         Sub = suffix(S,5),
         Sender ! {message,Sub}, 
         Sender ! {message,Sub},
         Sender ! {message,Sub},
	 ecco()
   end.

start() ->
   Person = 'spawn'(helloworld,person,[]),
   Ecco = 'spawn'(helloworld,ecco,[]),
   Person ! {start,Ecco}.