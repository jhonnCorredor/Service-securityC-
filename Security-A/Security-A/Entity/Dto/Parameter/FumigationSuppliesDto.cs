using Entity.Model.Operational;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Entity.Dto.Parameter
{
    public class FumigationSuppliesDto
    {
        public int Id { get; set; }
        public string Dose { get; set; }
        public int SuppliesId { get; set; }
        public int FumigationId { get; set; }
        public bool State { get; set; }
    }
}
