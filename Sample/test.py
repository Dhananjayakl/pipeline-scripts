# n= 2
# while n < 10:
#     print(n)
#     n+=1
# print("loop ended")
# string = "hello World"
# reversed_string = string[::-1]
# print(reversed_string)

num=int(input())
tem=True
for i in range(2,num):
    if(num%i==0):
        tem=False

if(tem):
    print("number is prime")

