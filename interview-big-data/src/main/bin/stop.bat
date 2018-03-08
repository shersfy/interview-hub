@echo off

rem author: yuehan1@lenovo.com
rem date: 2016-06-08

set "clientStr=org.interview.big.data.mapreduce.APP"

for /f "tokens=1,2 delims= " %%i in ('jps -l') do (
    if %%j==%clientStr% taskkill /pid %%i /f /t 
)

echo big data stopped
pause