import psutil


def getCPU():
	return psutil.cpu_percentage()

def getMemory():
	return psutil.virtual_memory()._asdict()

def logData(filename, msg):
	with open(filename, "a") as f:
		f.write(msg)