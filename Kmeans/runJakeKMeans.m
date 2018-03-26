function [ errors ] = runJakeKMeans( inputFile, k, outputFile, includePlots, formatSpec)
%runJakeKMeans loads data and executes jakeKMeans, writing results to the
%output file.
%  

%% Validate Input

if nargin < 3
   disp('Invalid Input');
   return
end

if nargin < 4
    includePlots = false;
end

if nargin < 5
    formatSpec = '%d%d%d%d%C%C';   
end



%% Load Data

if strcmp(inputFile, 'movies_metadata.csv')
    if evalin( 'base', 'exist(''moviesmetadata'',''var'') == 1' )
        dataTable = evalin('base','moviesmetadata');
    else
        loadDataScript
        prepData
        dataTable = moviesmetadata;
    end
else
    dataTable = readtable(inputFile,'Delimiter',',','Format',formatSpec);
end

%CLEAN DATA (Turn categorical data into integers, remove non-numbers)
i = 1;
while i <= size(dataTable,2)
    if isnumeric(dataTable{1,i})
        i = i + 1;
    elseif iscategorical(dataTable{1,i})
        undefined = isundefined(dataTable{1,i});
        [c,ia,ib] = unique(dataTable(:,i));
        dataTable(:,i) = []; %delete row
        if ~undefined
            dataTable(:,end+1) = table(ib); %insert ids
        end
    else %delete non-numeric data
        dataTable(:,i) = []; %delete row
    end 
end
 

data = table2array(dataTable);
data = double(data);

%Normalize data onto range -1 to 1
minVals = min(data);
maxVals = max(data);
diffs = maxVals - minVals;
data = (data - minVals) ./ diffs;



%% Run jake K Means

[ids, centers, squareError, errors] = jakeKMeans(data, k, includePlots, includePlots); 
sse = errors;

%% Write Data
fileID = fopen(outputFile,'w');
fprintf(fileID,'%i\n',ids);
fprintf(fileID,'SSE: %f',squareError);
fclose(fileID);

end

