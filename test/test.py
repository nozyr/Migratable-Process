import subprocess
import os
import signal
import sys

Manager_Process = None
Test_process = None
Slv_processes = []

def ctl_c(signal, frame):
	print "terminating process"
	if Manager_Process:
		Manager_Process.terminate()
	if Test_process:
		Test_process.terminate()

	for slv_process in Slv_processes:
		slv_process.terminate()

	sys.exit()


def main():
	os.chdir('../src')
	os.system("javac -d ../bin/ ./*.java")
	slv_1_args = ["java", "-cp", "../bin", "SlaveNode", "1441"]
	slv_2_args = ["java", "-cp", "../bin", "SlaveNode", "1442"]
	mng_args = ["java", "-cp", "../bin", "ProcessManager"]
	test_args = ["java", "-cp", "../bin", "Test"]

	Slv_processes.append(subprocess.Popen(slv_1_args))
	Slv_processes.append(subprocess.Popen(slv_2_args))
	Manager_Process = subprocess.Popen(mng_args)
	Test_process = subprocess.Popen(test_args)

	while True:
		pass

signal.signal(signal.SIGINT, ctl_c)

if __name__ == '__main__':
	main()