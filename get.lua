
counter = 0

-- request = function()
--     wrk.method = "GET"
--     counter = (counter + 1)
--     counter_str = tostring(counter)
--     key = "key" .. counter_str
--     path = "/keys/" .. key
--     --io.write(string.format("probe %s %d\n", key, counter_str))
--     return wrk.format(nil, path)
-- end

request = function()
    wrk.method = "GET"
--     counter = (counter + 1)
--     counter_str = tostring(counter)
    key = "key0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456"
    path = "/keys/" .. key
    --io.write(string.format("probe %s %d\n", key, counter_str))
    return wrk.format(nil, path)
end


done = function(summary, latency, requests)
    io.write(string.format("total requests: %d, 95 p: %d\n", summary.requests, latency:percentile(95.0)))
end

-- response = function(status, headers, body)
--     io.write(string.format("response status-%s body %s\n", status, body))
-- end