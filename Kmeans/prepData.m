%columns of interest:
titles = ['budget' 'popularity' 'revenue' 'runtime' 'vote_average' 'vote_count'];
%titles = ["budget","popularity","release date","revenue","runtime","vote_average","vote_count"];

indices = [3 11 16 17 23 24];


numCategories = size(indices,2);
numEntries = size(t.adult,1);

%Build X array of data of interest from table of all metadata
X = zeros(numEntries,numCategories);
%Xi = moviesmetadata.(indices);
for i = 1:numCategories
   index = indices(i);
   X(:,i) = t.(index);
end

%remove rows with NaN data
X(any(isnan(X),2),:) = [];

%noralize data from -1 to 1
minVals = min(X);
maxVals = max(X);
diffs = maxVals - minVals;
X = (X - minVals) ./ diffs;





