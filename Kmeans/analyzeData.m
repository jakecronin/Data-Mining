%Clean data
%Run kmeanse

maxClusters = 10;
errors = zeros(maxClusters,1);

ds = zeros(maxClusters,1);

close all;
figure;
%Calculate SSE for clustering 1-10
for k = 1:maxClusters
    %run kmeans, track sse as # of clusters increases
    %[idx,C,sumd,D] = kmeans(X(:,1:2),k);
    [idx,C,sumd,D] = kmeans(X,k);
    
    
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
    
    %Try jake kmeans
    [ids,centers,errors(k,1)] = jakeKMeans(X,k);

    
    %plot data
    subplot(round(sqrt(maxClusters))+1,round(sqrt(maxClusters))+1,k);
    hold on;
    for c = 1:k %Plot each cluster
        plot(X(ids==c,1),X(ids==c,2),'.','MarkerSize',12)
        plot(centers(:,1),centers(:,2),'kx','MarkerSize',15,'LineWidth',3) 
    end
    hold off;
    title 'Cluster Assignments and Centroids';

end



%Plot Stuff


figure;
semilogy(errors);
hold off;




%legend('Cluster 1','Cluster 2','Centroids','Location','NW')




