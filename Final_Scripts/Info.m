function [result] = Info(a,b)
%Info calculates information entropy
%   Info(a,b) = -(a/total)log(a/total) - (b/total)log(b/total)

total = a+b;

if a == 0 || b == 0
    result = 0;
else
    result = -(a/total) * log(a/total) / log(2)  - (b/total) * log(b/total) / log(2);
end


end

