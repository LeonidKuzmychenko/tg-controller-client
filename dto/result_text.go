package dto

type ResultString struct {
	Result
	Data string `json:"data"`
}

func (ResultString) IsResult() {}
