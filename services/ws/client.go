package ws

import (
	"desktop-tg-client/services/commands"
	"desktop-tg-client/utils"
	"log"
	"sync/atomic"
	"time"

	"github.com/gorilla/websocket"
)

// WebSocketClient объединяет:
// - OkHttpWsClient
// - OkHttpListener
// - ReconnectManager
type WebSocketClient struct {
	url          string
	conn         *websocket.Conn
	reconnecting atomic.Bool
	manualClose  atomic.Bool
}

// Глобальный singleton, как Java enum INSTANCE
var WS = &WebSocketClient{}

// ------------------------------------
// INIT
// ------------------------------------

func (c *WebSocketClient) Init() {
	c.url = c.buildUrl()
	c.manualClose.Store(false)
	c.reconnecting.Store(false)

	// Первая попытка подключения
	go c.safeConnect()
}

func (c *WebSocketClient) buildUrl() string {
	return utils.SocketUrl() + "?key=" + utils.Key()
}

// ------------------------------------
// PUBLIC API
// ------------------------------------

func (c *WebSocketClient) ReloadKeyAndReconnect() {
	log.Println("[WS] Reloading key and reconnecting...")

	c.url = c.buildUrl()
	c.manualClose.Store(false)
	c.reconnecting.Store(false)

	c.Close()
	go c.safeConnect()
}

func (c *WebSocketClient) Close() {
	log.Println("[WS] Closing WebSocket client...")

	c.manualClose.Store(true)
	c.reconnecting.Store(false)

	if c.conn != nil {
		_ = c.conn.WriteControl(websocket.CloseMessage,
			websocket.FormatCloseMessage(websocket.CloseNormalClosure, "Client closing"),
			time.Now().Add(time.Second))
		_ = c.conn.Close()
		c.conn = nil
	}

	log.Println("[WS] Closed.")
}

// ------------------------------------
// CONNECT + RECONNECT
// ------------------------------------

func (c *WebSocketClient) safeConnect() {
	// Автоматическая попытка подключения — НЕ manual close
	c.manualClose.Store(false)

	log.Println("[WS] Connecting:", c.url)

	conn, _, err := websocket.DefaultDialer.Dial(c.url, nil)
	if err != nil {
		log.Println("[WS] Connect error:", err)
		c.scheduleReconnect()
		return
	}

	c.conn = conn
	log.Println("[WS] Connected!")

	go c.readLoop()
}

func (c *WebSocketClient) scheduleReconnect() {
	if c.manualClose.Load() {
		log.Println("[WS] Reconnect cancelled — manual close")
		return
	}

	if !c.reconnecting.CompareAndSwap(false, true) {
		// уже есть запланированный reconnect
		return
	}

	log.Println("[WS] Reconnecting in 3s...")

	time.AfterFunc(3*time.Second, func() {
		c.reconnecting.Store(false)

		if !c.manualClose.Load() {
			c.safeConnect()
		}
	})
}

// ------------------------------------
// READ LOOP (аналог OkHttpListener.onMessage)
// ------------------------------------

func (c *WebSocketClient) readLoop() {
	for {
		msgType, data, err := c.conn.ReadMessage()

		if err != nil {
			log.Println("[WS] Read error:", err)
			c.scheduleReconnect()
			return
		}

		switch msgType {
		case websocket.TextMessage:
			text := string(data)
			log.Println("[WS] REQUEST:", text)
			commands.Commands.Analyze(text)

		case websocket.BinaryMessage:
			text := string(data)
			log.Println("[WS] REQUEST(BYTE):", text)
			commands.Commands.Analyze(text)
		}
	}
}
