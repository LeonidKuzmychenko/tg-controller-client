package dto

type Result struct {
	Status string `json:"status"`
}

func (Result) IsResult() {}
