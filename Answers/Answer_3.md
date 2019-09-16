## Exercise 3.1
1. OS:   Mac OS X; 10.14.6; x86_64
   
   JVM:  Oracle Corporation; 11.0.2
   
   CPU:  2,6 GHz Intel Core i7; 8 "cores"
   
   Date: 2019-09-15T13:48:31+0200
   
   **Result: **
   
   Mark1
      0,0 ns
   Mark2
     25,6 ns
   Mark3
     25,1 ns
     27,6 ns
     26,3 ns
     25,6 ns
     25,6 ns
     27,1 ns
     27,5 ns
     27,0 ns
     24,4 ns
     24,5 ns
   Mark4
     26,3 ns +/-  0,993
   Mark5
    114,6 ns +/-    89,82          2
     66,1 ns +/-    32,24          4
     46,9 ns +/-    14,30          8
     38,6 ns +/-     6,92         16
     36,9 ns +/-     8,77         32
     35,7 ns +/-    15,38         64
     30,7 ns +/-     3,93        128
     30,0 ns +/-     2,05        256
     31,9 ns +/-     8,41        512
     32,7 ns +/-     7,73       1024
     74,5 ns +/-   145,08       2048
     30,7 ns +/-     4,84       4096
     28,6 ns +/-     4,01       8192
     27,0 ns +/-     0,37      16384
     26,0 ns +/-     2,58      32768
     26,8 ns +/-     1,82      65536
     24,3 ns +/-     0,45     131072
     25,7 ns +/-     0,38     262144
     24,5 ns +/-     0,66     524288
     24,2 ns +/-     0,19    1048576
     24,9 ns +/-     0,76    2097152
     25,1 ns +/-     1,19    4194304
     24,4 ns +/-     0,36    8388608
     25,1 ns +/-     0,71   16777216
   Mark6
   multiply                            428,1 ns     998,27          2
   multiply                            100,2 ns      46,73          4
   multiply                            105,5 ns      67,20          8
   multiply                             82,3 ns      26,99         16
   multiply                             96,0 ns      56,27         32
   multiply                             50,0 ns      24,87         64
   multiply                             39,8 ns       3,71        128
   multiply                             38,9 ns       1,85        256
   multiply                             39,3 ns       4,10        512
   multiply                             35,7 ns       0,55       1024
   multiply                             35,5 ns       0,23       2048
   multiply                             35,5 ns       0,38       4096
   multiply                             28,0 ns       0,98       8192
   multiply                             27,6 ns       0,02      16384
   multiply                             27,4 ns       0,69      32768
   multiply                             28,2 ns       1,81      65536
   multiply                             25,5 ns       0,34     131072
   multiply                             25,5 ns       0,21     262144
   multiply                             24,8 ns       0,83     524288
   multiply                             24,7 ns       0,87    1048576
   multiply                             24,9 ns       0,62    2097152
   multiply                             25,3 ns       1,23    4194304
   multiply                             24,2 ns       0,06    8388608
   multiply                             24,5 ns       0,52   16777216
   
2. Result: 

   OS:   Mac OS X; 10.14.6; x86_64

   JVM:  Oracle Corporation; 11.0.2

   CPU:  2,6 GHz Intel Core i7; 8 "cores"

   Date: 2019-09-15T15:01:09+0200

   pow                                  19,7 ns       0,36   16777216
   exp                                  22,0 ns       0,33   16777216
   log                                  11,9 ns       0,14   33554432
   sin                                  15,7 ns       1,21   16777216
   cos                                  14,7 ns       0,24   33554432
   tan                                  19,9 ns       0,17   16777216
   asin                                195,6 ns       3,36    2097152
   acos                                184,8 ns       2,52    2097152
   atan                                 44,1 ns       0,84    8388608



## Exercise 3.2

1. 
2. Uncontended lock                      2,9 ns       0,02  134217728