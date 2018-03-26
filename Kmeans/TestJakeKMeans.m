

close all;

%% Run KMeans on Iris Dataset
formatSpec = '%d%d%d%d%C';   

errorsOverIterations = runJakeKMeans('irisdata.dat',3,'irisout.dat',true,formatSpec);
irisErrorsFig = figure('NumberTitle', 'off', 'Name', 'Iris Data Errors over Iterations');
plot(errorsOverIterations);


%Run KMeans on Iris for k = 1 - 10
for i = 1:10
    e = runJakeKMeans('irisdata.dat',i,'irisout.dat',false,formatSpec);
    errs(i) = e(end);
end
irisItersFig = figure();
set(irisItersFig, 'NumberTitle', 'off', ...
    'Name', 'IRIS: SSE for Varying Number of Clusters');
semilogy(errs)
xlabel('Iterations')
ylabel('SSE')


%% Run KMeans on Movie Databse (very large. Takes a few minutes)
disp('Going to Load Movie Data. This will take several minutes');

%Load the data. Takes a few minutes
if ~exist('moviesmetadata')
    loadDataScript;
end

if ~exist('X')
    %Cleans the data. Also takes several minutes
    prepData;
end

%Quickly runs my kmeans and MATLAB's kmeans function with useful plots
analyzeData;
