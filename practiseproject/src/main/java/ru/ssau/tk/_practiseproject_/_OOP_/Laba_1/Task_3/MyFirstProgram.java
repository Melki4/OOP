class MyFirstClass {
	public static void main(String[] s){
		MySecondClass o = new MySecondClass(3, 5);

		System.out.println("Значения полей при начальной инициализации" +'\n' +
		"первое число : " + o.get_first_value() +'\n' + "второе число : " +
		 o.get_second_value() +'\n');

		System.out.println(o.operation());
		for (int i = 1; i <= 8; i++) {
 				for (int j = 1; j <= 8; j++) {
     				o.set_first_value(i);
     				o.set_second_value(j);
     				System.out.print(o.operation());
     				System.out.print(" ");
 			}
 			System.out.println();
		}

	}
}

class MySecondClass {

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