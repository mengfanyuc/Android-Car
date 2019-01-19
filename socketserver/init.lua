pwm.setup(1, 1000, 0) --左前
pwm.start(1)
pwm.setup(2, 1000, 0) --右前
pwm.start(2)
pwm.setup(4, 1000, 0) --左后
pwm.start(4)
pwm.setup(5, 1000, 0) --右后
pwm.start(5)

gpio.mode(6, gpio.OUTPUT) --正
gpio.mode(7, gpio.OUTPUT) --反
gpio.write(6, gpio.LOW)
gpio.write(7, gpio.LOW)

tmr.alarm(0, 1000, tmr.ALARM_SINGLE, function()
    dofile("wifi.lua")
end)
