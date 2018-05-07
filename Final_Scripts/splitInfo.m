function [result] = splitInfo(values)
%splitInfo calculates split info for given split options

total = sum(values);
result = 0;
for i = 1:size(values,2)
    intermediate = (values(i)/total) * log(values(i)/total) / log(2);
    result = result - intermediate;
end

end

