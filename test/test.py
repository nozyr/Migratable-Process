import os

def main():

	os.system("java -cp ../src/ SlaveNode")
	os.system("java -cp ../src/ SlaveNode2")
	os.system("java -cp ../src/ ProcessManager")

	os.system("java -cp ../src/ Test")
if __name__ == '__main__':
	main()