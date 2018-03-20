function [ ids, centers, squareError ] = jakeKMeans(data,k)
%jakeKMeans takes multidimensional data and the number of clusters, k, and
%assigns each row of data to a cluster in the ids matrix. The centers of
%the clusters are returned in the centers matrix, and the sum of squared
%error value is returned in sse


%choose k random data points as centers
[rows, vars] = size(data);
randPoints = randperm(rows);
centers = data(randPoints(1:k),:); %cluster center coordinates

ids = zeros(rows,1); %Cluster id for each row
dists = zeros(rows,1); %Distance to nearest row

%Assign initial cluster ids and get distances from nearest cluster
[dists, ids] = pdist2(centers,data,'euclidean', 'Smallest',1);

%Iterate until there are no changes to t
changed = 1;
while changed
    
    %get distance between data points and closest cluster and cluster id
    
    %Move each center to the geometric center of their cluster
    for c = 1:k %Get center of point cloud for each cluster
       centers(c,1) = mean(dists(ids==c));
    end
    
    %Calcualate new distances and re-assign clusters
    oldids = ids; %store old cluster ids
    [dists, ids] = pdist2(centers,data, 'euclidean','Smallest',1);
    
    %see if anything has changeds
    changed = (oldids ~= ids); %see if any ids have changed
end

squareError = sum(dists.^2);

end

