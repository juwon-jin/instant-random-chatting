var requests = require('config/requests');
var request = require('request');


module.exports = function(app) {



	app.get('/', function(req, res) {

		res.end("Node-Android-Chat-Project"); 
	});


	app.post('/signup',function(req,res){
		var name = req.body.name;
       		var mobno = req.body.mobno;
       		var password = req.body.password;
       		var first_install_moment = req.body.first_install_moment;
       		var connect_start_moment = req.body.connect_start_moment;
        	var reg_id = req.body.reg_id;
			
		requests.signup(name,mobno,password,first_install_moment,connect_start_moment,reg_id,function (found) {
			console.log(found);
			res.json(found);
	});		
	});

	app.post('/signin',function(req,res){
       	var mobno = req.body.mobno;
       		var password = req.body.password;
       		var connect_start_moment = req.body.connect_start_moment;
        	var reg_id = req.body.reg_id;
			
		requests.signin(mobno,password,connect_start_moment,reg_id,function (found) {
			console.log(found);
			res.json(found);
	});		
	});
	
	app.post('/send',function(req,res){
		var fromu = req.body.from;
		var fromn = req.body.fromn;
        	var to = req.body.to;
        	var msg = req.body.msg;

			
		requests.send(fromn,fromu,to,msg,function (found) {
			console.log(found);
			res.json(found);
	});		
	});

	app.post('/save_total_time',function(req,res){
		var mobno = req.body.mobno;
		var connect_finish_moment = req.body.connect_finish_moment;
		var total_connect_time = req.body.total_connect_time;
			
		requests.save_total_time(mobno, connect_finish_moment, total_connect_time, function (found) {
			console.log(found);
			res.json(found);
	});		
	});

	app.post('/load_total_time',function(req,res){
		var mobno = req.body.mobno;
			
		requests.load_total_time(mobno,function (found) {
			console.log(found);
			res.json(found);
	});		
	});

	app.post('/load_rank',function(req,res){
		var mobno = req.body.mobno;
			
		requests.load_rank(mobno, function (found) {
			console.log(found);
			res.json(found);
	});		
	});

	app.post('/getuser',function(req,res){
		var mobno = req.body.mobno;
			
		requests.getuser(mobno,function (found) {
			console.log(found);
			res.json(found);
	});		
	});

	app.post('/withdrawal',function(req,res){
		var mobno = req.body.mobno;

			
		requests.removeuser(mobno,function (found) {
			console.log(found);
			res.json(found);
	});		
	});

	
};




