matrix = [0 2/5 20/40; 0 1/5 0; 1 0 20/40; 1 2/5 1; 0 2/5 15/40; 0 3/5 20/40; 0 1 10/40; 0 1/5 0];
a = [1 1/5 0];

deltas = matrix - a;


squared = deltas .^ 2;
summed = sum(squared,2);
distances = summed .^ (1/2);



m = [16 1 4; 1 1 9; 1 16 4; 1 1 4; 1 1 9];

p1 = 0.7;
p2 = 0.3;
r = 3;
b = 1;

p1 = 1-p1^r
p1 = (1-p1^r)

p2 = 1-p2^r
p2 = (1-p2^r)




