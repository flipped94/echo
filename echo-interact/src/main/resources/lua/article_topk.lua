---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by rui.
--- DateTime: 2024/5/17 9:16
---

local key = KEYS[1]

local id = ARGV[1]

local exist = redis.call("EXISTS", key)
if exist == 1 then
    local contains = redis.call("TOPK.QUERY", key, id)
    if contains == 1 then
        redis.call("TOPK.INCRBY", key, id, 1)
        return 1
    else
        redis.call("TOPK.ADD", key, id)
        return 1
    end
else
    redis.call("TOPK.RESERVE", key, 50, 2000, 7, 0.9)
    redis.call("TOPK.ADD", key, id)
    return 1
end
