%DECISION TREE STUFF
% Calculating things for decision tree algorithms

% Information  - ID3
% Gain Ratio (C.45)
% Gini Index (CART)
% Variance reduction for continuous target variable (CART)

X = [];

%% Calculate Information Entropy
% H(y) = -sum( px .* log(px) ) with px = P(Y = yx)

entropy = -1 * sum(X .* log(X));

%% Information Entropy
% Gain(A) = Info(D) - InfoA(D)