// endianess.cc
#include <cstdio>
#include <cstdint>

void fct(/*code*/const char* *pstr) {
  *pstr = "hello world";
}

bool bigendian()
{
  int32_t val = 1;
  char* p = reinterpret_cast<char*>(&val);
  // for big endian, high byte appear first, so *p == 0
  // for little endian, low byte appear first, so *p == 1
  if (*p) printf("little endian\n");
  else printf("big endian\n");

  return *p == 0;
}

int main()
{
  const char* str;
  fct(/*code*/&str);
  printf("%s\n", str);

  bigendian();
}
