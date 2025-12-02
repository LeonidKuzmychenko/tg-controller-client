rsrc -ico icon.ico -o resource.syso
go build -ldflags "-H=windowsgui" -o app.exe