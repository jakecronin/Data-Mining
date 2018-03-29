%THIS CODE IS MY OWN WORK AND WAS DONE WITHOUT CONSULTING A TUTOR OR CODE WRITTEN BY OTHER STUDENTS
% - JAKE CRONIN 
function [ ids, centers, squareError, errors ] = jakeKMeans(data,k,plotClusters,plotCenters)
%jakeKMeans takes 2 dimensional array (rows X attributes) of data and the number of clusters, k, and
%assigns each row of data to a cluster in the ids matrix. The centers of
%the clusters are returned in the centers matrix, and the sum of squared
%error value is returned in sse

%%Validate Input and Initialize Args 

if nargin < 2
    disp('Argument error with jakeKMeans');
end

if nargin < 3
    plotClusters = false;
end

if nargin < 4
   plotCenters = false;
end

%choose k random data points as centers
[rows, vars] = size(data);
randPoints = randperm(rows);
centers = data(randPoints(1:k),:); %cluster center coordinates

%% Get Random Centers and assign Points to nearest Cluster

ids = zeros(rows,1); %Cluster id for each row
dists = zeros(rows,1); %Distance to nearest row

%Assign initial cluster ids and get distances from nearest cluster
[dists, ids] = pdist2(centers,data,'cityblock', 'Smallest',1);
i = 1;
errors(i) = sum(dists.^2);


%Save centers and ids if need to plot
if plotClusters
    pids(:,i) = ids;
    pcenters(i,:,:) = centers;
end

 if plotCenters
    centerfig = figure('NumberTitle', 'off', 'Name', 'Movement of Cluster Centroids');
 end

%% Iterate Re-Centering Clusters and Re-Assigning Data Points
changed = 1;
while changed
    i = i+1;
 
    %Move each center to the geometric center of their cluster
    for c = 1:k %Get center of point cloud for each cluster
       centers(c,1) = mean(data(ids==c));
    end
    
    %Calcualate new distances and re-assign clusters
    oldids = ids; %store old cluster ids
    [dists, ids] = pdist2(centers,data, 'cityblock','Smallest',1);
    dists(isnan(dists))=0;
    %see if anything has changed
    changed = ~(isequal(oldids, ids)); %see if any ids have changed
    errors(i) = sum(dists.^2);
    
    if plotClusters %Save ids and centers if requested
        pids(:,i) = ids;
        pcenters(i,:,:) = centers;
    end
    
    if plotCenters
        centerfig;
        view(3)
        hold on;
        if changed
            plot3(centers(:,1),centers(:,2),centers(:,3),'bx','MarkerSize',10,'LineWidth',2)
        else
            plot3(centers(:,1),centers(:,2),centers(:,3),'kx','MarkerSize',15,'LineWidth',3)
        end
    end
end
squareError = sum(dists.^2);

%% Plot Errors over iterations if Requested
if plotClusters
    clusterfig = figure('NumberTitle', 'off', 'Name', 'Cluster Assignments');
    view(3)
    iters = i;
    hold on;
    for j = 1:iters % Plot each iteration
        subplot(round(sqrt(iters))+1,round(sqrt(iters))+1,j);
        view(3);
        hold on;
        for c = 1:k %Plot each cluster
            ids = pids(:,j); %Point assignments for this cluster
            plot3(data(ids==c,1), data(ids==c,2), data(ids==c,3),'.','MarkerSize',12)
            plot3(pcenters(j,:,1),pcenters(j,:,2),pcenters(j,:,3),'kx','MarkerSize',15,'LineWidth',3)
        end
    end 
    hold off; 
end


end

