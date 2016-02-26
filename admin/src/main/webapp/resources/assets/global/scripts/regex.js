var regex_date = {
	"regex" : "(?!0000)[0-9]{4}-((0[1-9]|1[0-2])-(0[1-9]|1[0-9]|2[0-8])|(0[13-9]|1[0-2])-(29|30)|(0[13578]|1[02])-31)",
	"error" : "时间格式错误，或参数为空"
};
var regex_empty = {
	"notRegex":true,
	"error" : "参数不能为空"
};
var regex_number = {
	"regex" : "^[0-9]{1,8}$",
	"error" : "必须为数字"
};
var regex_username = {
	"regex" : "^[0-9]{1,8}$",
	"error" : "投资总金额1-99999999"
};
