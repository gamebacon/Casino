class homo {
	public static void main(String[] args) {

		int lol =  123400;
		String str = Integer.toString(lol);
		int newLol = str.replaceFirst("^0+(?!$)", "");

		System.out.println(newLol);
	}
}
