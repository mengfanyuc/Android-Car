pwm.setup(1, 1000, 0) --左前
pwm.start(1)
pwm.setup(2, 1000, 0) --右前
pwm.start(2)

gpio.mode(6, gpio.OUTPUT) --正
gpio.mode(5, gpio.OUTPUT) --反
gpio.write(6, gpio.LOW)
gpio.write(5, gpio.LOW)

tmr.alarm(0, 1000, tmr.ALARM_SINGLE, function()
    dofile("wifi.lua")
end)
