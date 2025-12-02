package commands

import (
	"fmt"
	"tg-controller-client/dto"
	httpSender "tg-controller-client/services/http"
	"tg-controller-client/utils"
)

// Commands — аналог Java enum INSTANCE
var Commands = NewCommands()

type CommandsManager struct {
	firstCommand AbstractCommand
}

func NewCommands() *CommandsManager {
	return &CommandsManager{
		firstCommand: nil, // цепочку свяжем позже
	}
}

// SetFirstCommand — ты сам свяжешь цепочку и передашь первую команду
func (c *CommandsManager) SetFirstCommand(cmd AbstractCommand) {
	c.firstCommand = cmd
}

// Analyze — всё как в Java: вызвать первую команду, определить тип результата, отправить HTTP
func (c *CommandsManager) Analyze(command string) {
	if c.firstCommand == nil {
		fmt.Println("First command is not set!")
		return
	}

	result := c.firstCommand.Analyze(command)

	switch res := result.(type) {

	case dto.ResultString:
		// аналог sendObject
		httpSender.HR.SendObject(
			utils.Key(),
			command,
			res,
		)

	case dto.ResultImages:
		httpSender.HR.SendImagesMultipart(
			res.Images,
			utils.Key(),
			command,
			res.Status,
		)

	case dto.Result:
		// аналог sendText
		httpSender.HR.SendText(
			utils.Key(),
			command,
			res.Status,
		)

	default:
		fmt.Println("Unknown result type")
	}
}
