import re
import matplotlib.pyplot as plt

def addTimesVar(lines):
    task1 = []
    task1Var = []
    task2 = []
    task2Var = []
    for line in lines:
        if line.startswith("#"):
            continue
        words = line.split()
        if line.startswith("countParTask1"):
            task1.append(float(words[2].replace(',','.')))
            task1Var.append(float(words[4].replace(',','.')))
        else:
            task2.append(float(words[2].replace(',','.')))
            task2Var.append(float(words[4].replace(',','.')))
            
    return task1,task1Var,task2,task2Var

def readFromFile(filename):
    f = open(filename,'r')
    lines = f.readlines()
    f.close()
    return addTimesVar(lines)


def plotAndSvae(t1,t2,st1,st2):
	x = range(1,101)
	fig, axs = plt.subplots(2, 2,figsize=(12,8))
	axs[0,0].plot(x,t1,label="task1")
	axs[0,0].plot(x,t2,label = "task2")
	axs[0,0].set_title("CachedThreadPool")
	axs[0,0].legend()

	axs[0,1].plot(x,st1,label="task1")
	axs[0,1].plot(x,st2,label = "task2")
	axs[0,1].set_title("WorkStealingPool")
	axs[0,1].legend()

	axs[1,0].plot(x,t1,label="CachedThreadPool")
	axs[1,0].plot(x,st1,label = "WorkStealingPool")
	axs[1,0].set_title("multiple (Runnable) tasks")
	axs[1,0].legend()


	axs[1,1].plot(x,t2,label="CachedThreadPool")
	axs[1,1].plot(x,st2,label = "WorkStealingPool")
	axs[1,1].set_title("multiple Callable<Long> tasks")
	axs[1,1].legend()


	for ax in axs.flat:
	    ax.set(xlabel='task numbers', ylabel='time: us')
	for ax in axs.flat:
	    ax.label_outer()

	fig.savefig('a5.png')


if __name__ == "__main__":
	t1,tv1,t2,tv2 = readFromFile( "timeResult/CachedThreadPool.txt")
	st1,stv1,st2,stv2 = readFromFile("timeResult/WorkStealingPool.txt")
	plotAndSvae(t1,t2,st1,st2)


