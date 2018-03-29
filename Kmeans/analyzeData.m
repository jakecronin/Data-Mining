%THIS CODE IS MY OWN WORK AND WAS DONE WITHOUT CONSULTING A TUTOR OR CODE WRITTEN BY OTHER STUDENTS
% - JAKE CRONIN 

disp('Going to analyze IMDB data')

maxClusters = 10;
errors = zeros(maxClusters,1);
jerrors = zeros(maxClusters,1);

ds = zeros(maxClusters,1);

clustersFig = figure();
%Calculate SSE for clustering 1-10
for k = 1:maxClusters
    %run kmeans, track sse as # of clusters increases
    %[idx,C,sumd,D] = kmeans(X(:,1:2),k);
    [idx,C,sumd,D] = kmeans(X,k, 'Distance', 'cityblock');
    
    
    %C is centroid locations
    %D is distance from each point to each centroid [distance,centroidID]
    ds(k,1) = sum(sumd);
    %calculate SSE

    %Clean data for easy distance indexing
    numPoints = size(idx,1);
    distancesFromCenter = zeros(numPoints,1);
    for i = 1:numPoints
        cluster = idx(i);
        distancesFromCenter(i,1) = D(i,cluster); %get distance from point to its cluster
    end

    %get squared distances
    distancesFromCenter = distancesFromCenter.^2;

    %summate distances
    sse = sum(distancesFromCenter);
    errors(k,1) = sse;
    
    %Run jake kmeans
    [ids,centers,jerrors(k,1), jitererrors] = jakeKMeans(X,k);

    
    %plot data
    clustersFig;
    subplot(min(maxClusters,4),floor(maxClusters/4)+(mod(maxClusters,4)~=0),k);
    view(3);
    hold on;
    for c = 1:k %Plot each cluster
        plot3(X(ids==c,1),X(ids==c,2),X(ids==c,3),'.','MarkerSize',10);
        plot3(centers(:,1),centers(:,2),centers(:,3),'kx','MarkerSize',15,'LineWidth',3);
        %scatter3(X(ids==c,1),X(ids==c,2),X(ids==c,3));
        %scatter3(centers(:,1),centers(:,2),centers(:,3));
        %plot(X(ids==c,1),X(ids==c,2),'.','MarkerSize',12);
        %plot(centers(:,1),centers(:,2),'kx','MarkerSize',15,'LineWidth',3);
    end
    hold off;

end

set(clustersFig, 'NumberTitle', 'off', ...
    'Name', 'IMDB: Custer Assignemnts and Centroids');

%Plot Stuff


figure();
semilogy(jerrors);
title('IMDB: Jake kmeans errors with Manhattan Distance')
xlabel('Number of Clusters')
ylabel('Sum of Squared Errors')

figure();
semilogy(errors);
title('IMDB: MATLAB kmeans errors with Manhattan Distance')
xlabel('Number of Clusters')
ylabel('Sum of Squared Errors')
hold off;

figure();
semilogy(jitererrors)
title(sprintf('IMDB: Jake kmeans error by iteration for %i clusters', k));
xlabel('Iterations')
ylabel('Sum of Squared Errors')



%legend('Cluster 1','Cluster 2','Centroids','Location','NW')




