// endianess.cc
#include <cstdio>
#include <cstdint>
#include <string>

void fct(/*code*/const char* *pstr) {
  *pstr = "hello world";
}

bool bigendian()
{
  int32_t val = 0x11223344;
  char* p = reinterpret_cast<char*>(&val);
  // for big endian, high byte appear first, so *p == 0x11
  // for little endian, low byte appear first, so *p == 0x44
  if (*p == 0x44) printf("little endian\n");
  else printf("big endian\n");

  return *p == 0x11;
}

std::string itoa(int32_t num) {
  int64_t l = num;
  bool neg = false;
  if (l < 0) { l = -l; neg = true; }
  std::string res;
  do {
    res += l % 10 + '0';
    l /= 10;
  } while (l);
  if (neg) res += '-';
  // reverse
  for (int a = 0, b = res.length()-1; a < b; ++a, --b) {
    char ch = res[a];
    res[a] = res[b];
    res[b] = ch;
  }
  return res;
}

int main()
{
  const char* str;
  fct(/*code*/&str);
  printf("%s\n", str);

  bigendian();

  {
    int32_t i = 123;
    printf("itoa(%d) = %s\n", i, itoa(i).c_str());
  }
  {
    int32_t i = 0;
    printf("itoa(%d) = %s\n", i, itoa(i).c_str());
  }
  {
    int32_t i = -123;
    printf("itoa(%d) = %s\n", i, itoa(i).c_str());
  }
}
