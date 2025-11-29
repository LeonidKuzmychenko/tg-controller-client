package commands

func BuildChain() AbstractCommand {
	cmdIP := &CommandIP{}
	cmdShot := &CommandScreenshot{}
	cmdShutdown := &CommandShutdown{}

	cmdIP.SetNext(cmdShot)
	cmdShot.SetNext(cmdShutdown)

	return cmdIP
}
