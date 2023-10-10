
counter = 0

request = function()
    wrk.method = "PUT"
    wrk.headers["Content-Type"] = "application/json"
    counter = (counter + 1)
    counter_str = tostring(counter)
    key = "key-" .. counter_str
    value = "value-" .. counter_str
    -- change this to your path for Set e.g.
    path = "/keys/" .. key .. "/value/" .. value
--     body = value
    return wrk.format(nil, path, nil, nil)
end

-- request = function()
--     wrk.method = "PUT"
--     wrk.headers["Content-Type"] = "application/json"
--     str = "key0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456"
--     -- change this to your path for Set e.g.
--     path = "/keys/" .. str .. "/value/" .. str
-- --     body = str
-- --     return wrk.format(nil, path, nil, body)
--     return wrk.format(nil, path, nil, nil)
-- end


done = function(summary, latency, requests)
    io.write(string.format("total requests: %d, 95 p: %d\n", summary.requests, latency:percentile(95.0)))
end
