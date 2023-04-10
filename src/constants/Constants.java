package constants;

public interface Constants {
	
	public final String STYLE_FOR_SERVICENAMES = "<style type='text/css'>"
            +"body{margin: 0px;padding: 0px;background-image: url(127.0.0.1:8444/chat/reset2.PNG);text-align: center;font-family: Arial;}"
            +"#topDiv{width: 100%;height: 100px;background-color: #a4a0a0;box-shadow: 0px 15px 10px #a4a0a0;text-align: center;}"
            +"#generalDiv{margin-left: auto;margin-right: auto;margin-top: 10em;width: 500px;background-color: #b7b4b4;border-radius: 25px;box-shadow: 0px 10px 20px black;text-align: center;border: 1px solid #b7b4b4;}"
            +"a{text-decoration: none;}"
            +".linkStyle{padding: 5px;border: 1px solid black;margin-top: 10px;margin-bottom: 10px;color: black;background-color: #99D9EA;border-radius: 10px;}"
            +"#jfw{width: 250px;margin-left: auto;margin-right: auto;}"
            +"</style>";

	public final String STYLE_FOR_HANDLER = "<style type='text/css'>"
			+"body {margin: 0px;padding: 0px;background-image: url(127.0.0.1:8444/chat/reset2.PNG);font-family: Arial;}"
            +"#topDiv {width: 100%;height: 100px;background-color: #a4a0a0;box-shadow: 0px 15px 10px #a4a0a0;text-align: center;opacity: 1;}"
            +"#ferstDiv {width: 500px;margin-left: auto;margin-right: auto;margin-top: 15em;background-color: #b7b4b4;border-radius: 25px;box-shadow:0px 10px 20px black;border: 1px solid #b7b4b4;}"
            +"#leftDiv {float: left;width: 125px;}"
            +"#rightDiv {float: right;width: 125px;}"
            +"#clearDiv {clear: both;}"
            +".inputStyle {margin-top: 15px;}"
            +"#submit {margin-bottom: 10px;margin-left: 100px;}"
            +"</style>";
	
	public final String HTML_AND_STYLE_FOR_MAINSERVLET_PART_1 = "<!DOCTYPE html><html lang='en'><head><meta charset='utf-8' /><title>Chat</title>"
			+"<style type='text/css'>body{margin: 0px;padding: 0px;font-family: Arial;background-image: url(127.0.0.1:8444/chat/reset2.PNG);text-align: center;}"
            +"a{text-decoration: none;}"
            +"#topDiv {width: 100%;height: 100px;background-color: #a4a0a0;box-shadow: 0px 15px 10px #a4a0a0;text-align: center;}"
            +"#ferstDiv {width: 500px;margin-left: auto;margin-right: auto;margin-top: 15em;background-color: #b7b4b4;border-radius: 25px;box-shadow:0px 10px 20px black;border: 1px solid #b7b4b4;word-wrap: break-word;}"
            +"#secondDiv {width: 500px;margin-left: auto;margin-right: auto;margin-top: 2em;background-color: #b7b4b4;border-radius: 25px;box-shadow:0px 10px 20px black;border: 1px solid #b7b4b4;word-wrap: break-word;}"
            +"#ps{padding: 5px;border: 1px solid black;background-color: #99D9EA;border-radius: 10px;color: black;}"
            +"#jfps{margin-left: auto;margin-right: auto;width: 220px;}"
            +"</style></head>"
            +"<body><div id='topDiv'></div><h1 style='text-align: center;font-size: 60px;margin-top: -20px;'>Chat online</h1>"
            +"<div id='ferstDiv'><h2 style='padding-top: 4px;'>Result</h2><p>";
	
	public final String HTML_AND_STYLE_FOR_MAINSERVLET_PART_2 = "</p></div><div id='secondDiv'><h2 style='padding-top: 4px;'>JSON Result</h2><p>";
	
	public final String HTML_AND_STYLE_FOR_MAINSERVLET_PART_3 = "</p><div id='jfps'><a href='HBaseTesterChat'><p id='ps'>Back to services list</p></a></div></div></body></html>";
	
	public final String STYLE_FOR_KAFKA = "<style type='text/css'>body {margin: 0px;padding: 0px;background-image: url(reset2.PNG);font-family: Arial;}"
            +"#topDiv {width: 100%;height: 100px;background-color: #a4a0a0;box-shadow: 0px 15px 10px #a4a0a0;text-align: center;opacity: 1;}"
            +"#ferstDiv {width: 500px;margin-left: auto;margin-right: auto;margin-top: 15em;background-color: #b7b4b4;border-radius: 25px;box-shadow:0px 10px 20px black;}"
            +".inputStyle {margin-top: 15px;}"
            +"#submit {margin-bottom: 10px;margin-left: 100px;}</style>";
}
