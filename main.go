//go:build windows

package main

import (
	"log"
	"tg-controller-client/services/commands"
	"tg-controller-client/services/ws"
	"tg-controller-client/ui"
	"tg-controller-client/utils"
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
