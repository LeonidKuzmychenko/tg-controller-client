//go:build windows

package main

import (
	"awesomeProject/services/commands"
	"awesomeProject/services/ws"
	"awesomeProject/ui"
	"awesomeProject/utils"
	"log"
)

func main() {
	// ОДИНАКОВЫЙ ИНСТАНС
	if !ensureSingleInstance() {
		log.Println("[APP] Already running. Exiting...")
		return
	}

	// ИНИЦИАЛИЗАЦИЯ KEY MANAGER
	if err := utils.KM.Init(); err != nil {
		log.Fatal(err)
	}
	log.Println("KEY =", utils.Key())

	// СБОРКА ЦЕПОЧКИ
	first := commands.BuildChain()
	commands.Commands.SetFirstCommand(first)

	// WEBSOCKET
	ws.WS.Init()

	// ТРЕЙ
	ui.StartTray()
}
