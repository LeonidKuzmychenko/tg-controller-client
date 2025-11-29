//go:build windows

package main

import (
	"desktop-tg-client/services/commands"
	"desktop-tg-client/services/ws"
	"desktop-tg-client/ui"
	"desktop-tg-client/utils"
	"log"
)

func main() {
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
