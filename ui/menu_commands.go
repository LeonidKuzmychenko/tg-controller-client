package ui

import (
	"fmt"
	"os"
	"tg-controller-client/services/ws"
	"tg-controller-client/utils"

	"github.com/atotto/clipboard"
	"github.com/getlantern/systray"
)

type MenuCommands struct{}

var Menu = &MenuCommands{}

// -----------------------------
// SHUTDOWN
// -----------------------------
func (m *MenuCommands) Shutdown() {
	fmt.Println("[UI] Shutdown")

	// Закрываем WebSocket-клиента (он сам остановит reconnect’ы)
	ws.WS.Close()

	// Закрываем трей
	systray.Quit()

	// Завершаем процесс
	os.Exit(0)
}

// -----------------------------
// REGENERATE KEY
// -----------------------------
func (m *MenuCommands) RegenerateKey() {
	fmt.Println("[UI] Regenerate key")

	// Генерируем новый ключ
	utils.KM.RegenerateKey()

	// Переподключаем WebSocket с новым ключом
	ws.WS.ReloadKeyAndReconnect()
}

// -----------------------------
// COPY KEY
// -----------------------------
func (m *MenuCommands) CopyKey() {
	key := utils.Key()
	fmt.Println("[UI] Copy key:", key)

	// Копирование в буфер обмена
	err := clipboard.WriteAll(key)
	if err != nil {
		fmt.Println("[UI] Clipboard error:", err)
	}
}
