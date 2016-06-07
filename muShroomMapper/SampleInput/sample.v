module m1(finput in1,in2,in3, foutput out1);     
	wire w1;
	assign w1 = in1 + in2;  
	assign out1 = w1 + in3;	
endmodule
