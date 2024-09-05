using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Entity.Model.Parameter
{
    public class City
    {
        public int Id { get; set; }
        public string Code { get; set; }
        public string Name { get; set; }    
        public string Description { get; set; }
        public int DepartamentId { get; set; }
        public Departament Departament {  get; set; }
        public Boolean State { get; set; }
        public DateTime Created_at { get; set; }
        public DateTime? Updated_at { get; set; }
        public DateTime? Deleted_at { get; set; }
    }
}
