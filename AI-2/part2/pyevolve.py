from pyevolve import *
import math
 
error_accum = Util.ErrorAccumulator()
 
# This is the functions used by the GP core,
# Pyevolve will automatically detect them
# and the they number of arguments
def gp_add(a, b): return a+b
def gp_sub(a, b): return a-b
def gp_mul(a, b): return a*b
def gp_sqrt(a):   return math.sqrt(abs(a))

xyData = []
with open('regression.txt', 'r') as f:
   f.readline()
   f.readline()
   line = f.readline()
   while line:
      xyData.append(map(float, line.split()))
      line = f.readline()

print xyData
 
for x, y in xyData:
   print x,y

def eval_func(chromosome):
   global error_accum
   error_accum.reset()
   code_comp = chromosome.getCompiledCode()

   for x, y in xyData:
      # The eval will execute a pre-compiled syntax tree
      # as a Python expression, and will automatically use
      # the "y" variable (the terminal defined)
      evaluated     = eval(code_comp)
      target        = y
      error_accum += (target, evaluated)
   return error_accum.getRMSE()
 
def main_run():
   genome = GTree.GTreeGP()
   genome.setParams(max_depth=4, method="ramped")
   genome.evaluator.set(eval_func)
 
   ga = GSimpleGA.GSimpleGA(genome)
   # This method will catch and use every function that
   # begins with "gp", but you can also add them manually.
   # The terminals are Python variables, you can use the
   # ephemeral random consts too, using ephemeral:random.randint(0,2)
   # for example.
   ga.setParams(gp_terminals       = ['x', 'ephemeral:random.randint(1,2)'],
                gp_function_prefix = "gp")
   # You can even use a function call as terminal, like "func()"
   # and Pyevolve will use the result of the call as terminal
   ga.setMinimax(Consts.minimaxType["minimize"])
   ga.setGenerations(1000)
   ga.setMutationRate(0.25)
   ga.setCrossoverRate(0.5)
   ga.setPopulationSize(5000)
   ga.evolve(freq_stats=5)
 
   print ga.bestIndividual()
 
if __name__ == "__main__":
   main_run()