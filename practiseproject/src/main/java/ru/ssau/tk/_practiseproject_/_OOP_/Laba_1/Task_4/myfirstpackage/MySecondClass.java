package myfirstpackage;

public class MySecondClass {

	public MySecondClass(int f, int s){
	f_int = f;
	s_int = s;
}

	private int f_int;
	private int s_int;	

	public int get_first_value(){return f_int;}
	public int get_second_value(){return s_int;}

	public void set_first_value(int x) {
	f_int =x;
}
	public void set_second_value(int x) {
	s_int =x;
}
	public void set_value(int f, int s) {
	s_int =s;
	f_int =f;
}

	public int operation(){
	return f_int + s_int;
}

}