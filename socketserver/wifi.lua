wifi.setmode(wifi.SOFTAP)

cfg_ap = {
    ssid = "AndroidCar",
    pwd = "123456789"
}
wifi.ap.config(cfg_ap)

cfg_ip = {
    ip = "192.168.1.128",
    netmask = "255.255.255.0",
    gateway = "192.168.1.1"
}
wifi.ap.setip(cfg_ip)

TCPSever = net.createServer(net.TCP, 28800)

TcpConnectCnt = 0; --连接客户端计数
TcpSocketTable = {}; --socket表
NowSocket = nil; --当前socket
if(TCPSever) then
    TCPSever:listen(5000, function(socket)
        if  TcpConnectCnt == 4 then --socket4连接关闭socket0
            if  TcpSocketTable[0] ~= nil then 
                TcpSocketTable[0]:close()
                TcpSocketTable[0] = nil;
            end    
        else
            if  TcpSocketTable[TcpConnectCnt + 1] ~= nil then --有新socket关闭前一个socket
                TcpSocketTable[TcpConnectCnt + 1]:close()
                TcpSocketTable[TcpConnectCnt + 1] = nil;
            end 
        end
    
        TcpSocketTable[TcpConnectCnt] = socket;
        
        TcpConnectCnt = TcpConnectCnt + 1; --新建socket
        if  TcpConnectCnt == 5 then
            TcpConnectCnt = 0;
        end
        
        socket:on("receive", function(socket, data) 
            NowSocket = socket;
            control(data)
        end)

       socket:on("disconnection", function(sck, c) 
            for i = 0, 4 do
                if  TcpSocketTable[i] == sck then
                    TcpSocketTable[i] = nil;
                end
            end
        end)
    end)
end


PreDuty = 0;
PreAngle = 0;
PreDirect = 0;
PreDuty_old = 0xFF;
PreAngle_old = 0xFF;
PreDirect_old = 0xFF;

function control(data)   
    if  data:byte(1) == 0x4D and data:byte(2) == 0x46 and data:byte(3) == 0x59 
    and bit.band(checkdata(data),0xFF) == data:byte(8) then

        if NowSocket ~= nil then
            NowSocket:send(data:byte(7))
        end

        PreDuty = data:byte(6); --速度 0-100
        PreAngle = data:byte(5); --角度 0-180
        PreDirect = data:byte(4); --方向 1前进 2后退

        if PreDirect ~= 0 then
            stopcar = 0;
            stop_flag = 0;
        
            if math.abs(PreAngle - PreAngle_old) > 5 or math.abs(PreDuty - PreDuty_old) > 5 
                or PreDirect ~= PreDirect_old then    
                
                if PreDirect == 1 then
                    if PreDirect_old ~= PreDirect then
                        gpio.write(5, gpio.LOW)
                        tmr.delay(500000)
                        gpio.write(6, gpio.HIGH) --正转
                    end
                                      
                    if PreAngle >= 0 and  PreAngle < 90 then --右前
                        pwm.setduty(1, PreDuty * 10)
                        pwm.setduty(2, math.ceil(PreDuty * PreAngle * 10 / 90))
                    end
    
                    if PreAngle >= 90 and  PreAngle < 180 then --左前
                        pwm.setduty(1, math.ceil(PreDuty * (180 - PreAngle) * 10 / 90))
                        pwm.setduty(2, PreDuty * 10)
                    end 
                end
                    
                if PreDirect == 2 then                
                    if PreDirect_old ~= PreDirect then 
                        gpio.write(6, gpio.LOW)
                        tmr.delay(500000)
                        gpio.write(5, gpio.HIGH)  --反转 
                    end
                                  
                    if PreAngle >= 0 and  PreAngle < 90 then --右后
                        pwm.setduty(1, PreDuty * 10)
                        pwm.setduty(2, math.ceil(PreDuty * PreAngle * 10 / 90))
                    end
    
                    if PreAngle >= 90 and  PreAngle < 180 then --左后
                        pwm.setduty(1, math.ceil(PreDuty * (180 - PreAngle) * 10 / 90))
                        pwm.setduty(2, PreDuty * 10)
                    end
                end

                PreDuty_old = PreDuty;
                PreAngle_old = PreAngle;
                PreDirect_old = PreDirect;
            end 
        end
    end
end

function checkdata(data)
    count = 0;
    for i = 1, 7 do
        count = count + data:byte(i);
    end
    return count;
end

stopcar = 0;
stop_flag = 0;
tmr.alarm(1, 100, tmr.ALARM_AUTO, function() --停车处理
    if stopcar < 12 then
        stopcar = stopcar + 1;
    end
    
    if stopcar >= 12 and stop_flag == 0 then
        stop_flag = 1;     
        PreDuty = 0;
        PreAngle = 0;
        PreDirect = 0;
        PreDuty_old = 0xFF;
        PreAngle_old = 0xFF;
        PreDirect_old = 0xFF;
        gpio.write(6, gpio.LOW)  --停止
        gpio.write(5, gpio.LOW)
        pwm.setduty(1, 0)
        pwm.setduty(2, 0)
    end
end)
