package commands

import (
	"fmt"
	"io"
	"net"
	"net/http"
	"tg-controller-client/dto"
)

type CommandIP struct {
	BaseCommand
}

func (c *CommandIP) Condition() string {
	return "/ip"
}

func (c *CommandIP) Run(cmd string) dto.ResultInterface {
	localIP, err := getLocalIP()
	if err != nil {
		return dto.Result{Status: "Fail"}
	}

	externalIP, err := getExternalIP()
	if err != nil {
		return dto.Result{Status: "Fail"}
	}

	data := fmt.Sprintf(
		"Локальный IP: %s\nВнешний IP: %s\n",
		localIP, externalIP,
	)

	return dto.ResultString{
		Result: dto.Result{Status: "Success"},
		Data:   data,
	}
}

func (c *CommandIP) Analyze(cmd string) dto.ResultInterface {
	return c.BaseCommand.Analyze(cmd, c)
}

// ---------------- INTERNAL UTILS ----------------

// получение локального IP (аналог InetAddress.getLocalHost().getHostAddress())
func getLocalIP() (string, error) {
	addrs, err := net.InterfaceAddrs()
	if err != nil {
		return "", err
	}

	for _, addr := range addrs {
		if ipnet, ok := addr.(*net.IPNet); ok && !ipnet.IP.IsLoopback() {
			if ip4 := ipnet.IP.To4(); ip4 != nil {
				return ip4.String(), nil
			}
		}
	}

	return "", fmt.Errorf("no local IP")
}

// получение внешнего IP через https://api.ipify.org
func getExternalIP() (string, error) {
	resp, err := http.Get("https://api.ipify.org")
	if err != nil {
		return "", err
	}
	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return "", err
	}

	return string(body), nil
}
