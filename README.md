# Wiki

## Start

Сборка
>sbt assembly
> 
Запуск сервиса
>java -jar target/scala-2.12/WikiDump-assembly-0.1.jar
> 
Запуск парсилки дампа Вики
>java -cp target/scala-2.12/WikiDump-assembly-0.1.jar scripts.WikiParser dumpFileName.json
