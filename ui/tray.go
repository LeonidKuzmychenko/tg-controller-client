package ui

import (
	_ "embed"
	"log"

	"github.com/getlantern/systray"
)

//go:embed icon.ico
var embeddedIcon []byte

func StartTray() {
	log.Println("[UI] Tray starting...")
	systray.Run(onReady, onExit)
}

func onReady() {
	log.Println("[UI] Tray onReady()")

	// Устанавливаем иконку из embed
	if len(embeddedIcon) > 0 {
		systray.SetIcon(embeddedIcon)
	} else {
		log.Println("[UI] Embedded icon missing")
	}

	systray.SetTitle("Desktop Control")
	systray.SetTooltip("Desktop Control Client")

	mCopy := systray.AddMenuItem("Копировать ключ", "")
	mRegen := systray.AddMenuItem("Перегенерировать ключ", "")
	mQuit := systray.AddMenuItem("Выход", "")

	go func() {
		for {
			select {

			case <-mCopy.ClickedCh:
				log.Println("[UI] Menu clicked: COPY_KEY")
				handleCommand("COPY_KEY")

			case <-mRegen.ClickedCh:
				log.Println("[UI] Menu clicked: REGENERATE_KEY")
				handleCommand("REGENERATE_KEY")

			case <-mQuit.ClickedCh:
				log.Println("[UI] Menu clicked: EXIT")
				handleCommand("EXIT")
			}
		}
	}()
}

func onExit() {
	log.Println("[UI] Tray is shutting down...")
}
